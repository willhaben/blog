package at.willhaben.blog.cluster.service.infrastructure;

/**
 * Implement this interface if a service should have a
 * dedicated master node. It will receive a callback
 * if it should go to master mode or to slave mode.
 */
public interface ClusterService {

    void goToMasterMode();

    void goToSlaveMode();
}
