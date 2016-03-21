package at.willhaben.blog.cluster.service.infrastructure;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>This service is used to coordinate who is the master node and executes e.g. the batch jobs.
 * If there are multiple instances of a service e.g. messaging-server running and one
 * has to be the master node to avoid further synchronization for e.g. batch jobs.</p>
 * <p>To get a automatic fail over this service sends ping commands
 * and if there is no answer it callbacks all services that
 * implement the {@link ClusterService} interface to go to master mode.</p>
 */
@Service
class ClusterManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterManagerService.class);

    private static final String DEFAULT_ELECTION_ROOT_NODE = "Election";
    private static final String PROCESS_NODE_PREFIX = "/p_";
    private static final String ZOOKEEPER_PATH_SEPARATOR = "/";

    @Value("${cluster.name}")
    private String clusterName;

    @Value("${cluster.leaderElectionRootNode}")
    private String leaderElectionRootNode;

    @Value("${cluster.zkhost}")
    private String zookeeperURL = "localhost:2181";

    private ZooKeeperService zooKeeperService;

    private String processNodePath;
    private String watchedNodePath;

    private KeeperState previousState = KeeperState.Disconnected;

    private AtomicBoolean finishedInitialization = new AtomicBoolean(false);

    private ExecutorService service = Executors.newSingleThreadExecutor();

    @Autowired
    private List<ClusterService> clusterServices;

    private Status status;

    private enum Status {
        MASTER,
        SLAVE
    }

    @PostConstruct
    public synchronized void init() throws IOException, ClusterManagerException {

        LOGGER.info("ClusterManagerService: " + this.toString());

        if (StringUtils.isEmpty(clusterName)) {
            throw new ClusterManagerException("cluster name is empty. failover is not going to work.");

        }
        if (StringUtils.isEmpty(zookeeperURL)) {
            throw new ClusterManagerException("zookeeper url is empty. failover is not going to work.");
        }

        leaderElectionRootNode = ZOOKEEPER_PATH_SEPARATOR + this.clusterName + DEFAULT_ELECTION_ROOT_NODE;

        goToSlaveMode();
        zooKeeperService = new ZooKeeperService(zookeeperURL, new ProcessNodeWatcher());

        service.execute(this::initZooKeeper);

    }

    private void initZooKeeper() {

        final String rootNodePath = zooKeeperService.createNode(leaderElectionRootNode, false);

        if (rootNodePath == null) {
            throw new ClusterManagerStateException("Could not create or access election root node. Path: " + leaderElectionRootNode);
        }

        processNodePath = zooKeeperService.createNode(rootNodePath + PROCESS_NODE_PREFIX, true);

        if (processNodePath == null) {
            throw new ClusterManagerStateException("Could not create or access process node. Path: " + leaderElectionRootNode);
        }

        LOGGER.info("Process node created. Path: " + processNodePath);

        finishedInitialization.set(true);

        checkMasterIsAvailable();

    }

    private void checkMasterIsAvailable() {

        final List<String> childNodePaths = zooKeeperService.getChildren(leaderElectionRootNode, false);

        Collections.sort(childNodePaths);

        int index = childNodePaths.indexOf(processNodePath.substring(processNodePath.lastIndexOf('/') + 1));
        if (index == 0) {
            LOGGER.info("Going to master mode");
            goToMasterMode();
        } else {
            final String watchedNodeShortPath = childNodePaths.get(index - 1);

            watchedNodePath = leaderElectionRootNode + ZOOKEEPER_PATH_SEPARATOR + watchedNodeShortPath;

            LOGGER.info("Watching path: " + watchedNodePath);
            zooKeeperService.watchNode(watchedNodePath, true);
        }
    }

    class ProcessNodeWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {

            LOGGER.debug("Got Event from ZooKeeper: " + event + " Connection state: " + event.getState());

            if (event.getState() == KeeperState.Expired) {
                previousState = KeeperState.Disconnected;
                goToSlaveMode();
                try {
                    init();
                } catch (IOException | ClusterManagerException e) {
                    LOGGER.error("Failed to init after session timeout", e);
                }
                return;
            }

            if (event.getState() != KeeperState.SyncConnected) {
                previousState = event.getState();
                goToSlaveMode();
                return;
            }

            handleStateChange(event);
        }

        private void handleStateChange(WatchedEvent event) {
            if (finishedInitialization.get()) {

                if (previousState != KeeperState.SyncConnected && event.getState() == KeeperState.SyncConnected) {
                    previousState = event.getState();
                    checkMasterIsAvailable();
                }

                EventType eventType = event.getType();
                if (EventType.NodeDeleted == eventType && event.getPath().equalsIgnoreCase(watchedNodePath)) {
                    checkMasterIsAvailable();
                }
            }
        }
    }

    private void goToMasterMode() {
        LOGGER.info("Going to master mode ..." + this.toString());
        this.status = Status.MASTER;
        clusterServices.forEach(ClusterService::goToMasterMode);
    }

    private void goToSlaveMode() {
        LOGGER.info("Going to slave mode ... " + this.toString());

        if (this.status != Status.SLAVE) {
            clusterServices.forEach(ClusterService::goToSlaveMode);
            this.status = Status.SLAVE;
        }
    }

    @Override
    public String toString() {
        return "ClusterManagerService{" +
                "PROCESS_NODE_PREFIX='" + PROCESS_NODE_PREFIX + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", leaderElectionRootNode='" + leaderElectionRootNode + '\'' +
                ", processNodePath='" + processNodePath + '\'' +
                ", watchedNodePath='" + watchedNodePath + '\'' +
                ", zookeeperURL='" + zookeeperURL + '\'' +
                ", status=" + status +
                '}';
    }
}
