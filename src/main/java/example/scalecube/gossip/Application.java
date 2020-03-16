package example.scalecube.gossip;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterImpl;
import io.scalecube.cluster.ClusterMessageHandler;
import io.scalecube.cluster.membership.MembershipEvent;
import io.scalecube.cluster.transport.api.Message;
import io.scalecube.net.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Node that joins a cluster and sends messages via gossip.
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * Starts the node.
     *
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        final String name = getName(args);
        final Address seedAddress = getSeedAddress(args);
        final int clusterPort = getClusterPort(args);

        Cluster cluster = null;
        if (seedAddress == null) {
            // Seed Node
            cluster = new ClusterImpl()
                    .transport(tc -> tc.port(clusterPort))
                    .handler(cluster1 -> new ClusterMessageHandler() {
                        @Override
                        public void onMessage(Message message) {
                            LOG.info("Received Message: {}", message.data().toString());
                        }

                        @Override
                        public void onGossip(Message gossip) {
                            LOG.info("Received Data: {} from {}:{}", gossip.data().toString(),
                                    gossip.sender().host(), gossip.sender().port());
                        }

                        @Override
                        public void onMembershipEvent(MembershipEvent event) {
                            LOG.info("Membership Event: {}", event.toString());

                            if (event.isAdded()) {
                                cluster1.members().add(event.member());
                            } else if (event.isRemoved()) {
                                cluster1.members().remove(event.member());
                            }
                        }
                    })
                    .startAwait();
        } else {
            cluster = new ClusterImpl()
                    .membership(opts -> opts.seedMembers(seedAddress))
                    .handler(cluster1 -> new ClusterMessageHandler() {
                        @Override
                        public void onMessage(Message message) {
                            LOG.info("Received Message: {}", message.data().toString());
                        }

                        @Override
                        public void onGossip(Message gossip) {
                            LOG.info("Received Gossip: {}", gossip.data().toString());
                        }

                        @Override
                        public void onMembershipEvent(MembershipEvent event) {
                            LOG.info("Membership Event: {}", event.toString());

                            if (event.isAdded()) {
                                cluster1.members().add(event.member());
                            } else if (event.isRemoved()) {
                                cluster1.members().remove(event.member());
                            }
                        }
                    })
                    .startAwait();
        }

        start(name, cluster);
    }

    /**
     * Sends the integers 1 to 100.
     *
     * @param name node friendly name
     * @param cluster scalecube cluster
     */
    public static void start(String name, Cluster cluster) {
        Flux.range(1, 1000)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(integer -> {
                    cluster.otherMembers().forEach(member -> {
                        LOG.info("Sending Message: {}{} to {}", name, integer, member.address());
                        cluster.send(member, Message.fromData(name + integer));
                    });
                })
                .blockLast();
    }

    /**
     * Gets the name of the node from the command line arguments.
     *
     * @param args command line arguments
     * @return name of the node
     */
    private static String getName(String... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing required arguments: {name} {clusterPort}");
        }

        return args[0];
    }

    /**
     * Gets the cluster port of the node from the command line arguments.
     *
     * @param args command line arguments
     * @return cluster port of the node
     */
    private static int getClusterPort(String... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing required arguments: {name} {clusterPort}");
        }

        return Integer.parseInt(args[1]);
    }

    /**
     * Gets the seed address that this node will connect to when forming a cluster from the command line arguments.
     *
     * @param args command line arguments
     * @return seed node addresss
     */
    private static Address getSeedAddress(String... args) {
        if (args.length == 4) {
            return Address.create(args[2], Integer.parseInt(args[3]));
        }

        return null;
    }
}
