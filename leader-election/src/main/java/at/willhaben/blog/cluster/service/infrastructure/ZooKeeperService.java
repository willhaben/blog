package at.willhaben.blog.cluster.service.infrastructure;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * see https://github.com/SainTechnologySolutions/allprogrammingtutorials/tree/master/apache-zookeeper/leader-election
 */
class ZooKeeperService {

    private static final int SESSION_TIMEOUT = 3000;

    private final ZooKeeper zooKeeper;

    ZooKeeperService(String url, ClusterManagerService.ProcessNodeWatcher processNodeWatcher) throws IOException {
        zooKeeper = new ZooKeeper(url, SESSION_TIMEOUT, processNodeWatcher);
    }

    String createNode(String node, boolean ephemeral) {
        String createdNodePath;
        try {

            final Stat nodeStat = zooKeeper.exists(node, false);

            if (nodeStat == null) {
                createdNodePath = zooKeeper.create(node, new byte[0], Ids.OPEN_ACL_UNSAFE, (ephemeral ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.PERSISTENT));
            } else {
                createdNodePath = node;
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return createdNodePath;
    }

    boolean watchNode(final String node, final boolean watch) {

        boolean watched = false;
        try {
            final Stat nodeStat = zooKeeper.exists(node, watch);

            if (nodeStat != null) {
                watched = true;
            }

        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return watched;
    }

    List<String> getChildren(final String node, final boolean watch) {

        List<String> childNodes;

        try {
            childNodes = zooKeeper.getChildren(node, watch);
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return childNodes;
    }

}
