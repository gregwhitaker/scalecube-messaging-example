package example.scalecube.messaging;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterConfig;
import io.scalecube.cluster.ClusterImpl;
import io.scalecube.cluster.ClusterMessageHandler;
import io.scalecube.cluster.membership.MembershipEvent;
import io.scalecube.cluster.transport.api.Message;
import io.scalecube.net.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Service implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Service.class);

    private final String friendlyName;
    private final ClusterConfig config;

    private Service(Collection<Address> seedAddresses, int port, String friendlyName, String syncGroup) {
        this.friendlyName = friendlyName;
        this.config = new ClusterConfig()
                .transport(transportConfig -> transportConfig.port(port))
                .membership(membershipConfig -> {
                    if (!seedAddresses.isEmpty()) {
                        membershipConfig = membershipConfig.seedMembers(new ArrayList<>(seedAddresses));
                    }

                    if (syncGroup != null && !syncGroup.isEmpty()) {
                        membershipConfig = membershipConfig.syncGroup(syncGroup)
                                .syncInterval(1_000)
                                .syncTimeout(1_000);
                    }

                    return membershipConfig;
                });
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void run() {
        LOG.info("Starting Service: {}", friendlyName);

        Cluster c = new ClusterImpl(config).handler(cluster -> new ClusterMessageHandler() {
            @Override
            public void onMessage(Message message) {
                LOG.info("[{}] Received Message: '{}' from '{}'", friendlyName, message.data(), message.sender().toString());
            }

            @Override
            public void onGossip(Message gossip) {
                LOG.info("[{}] Received Gossip: '{}' from '{}'", friendlyName, gossip.data(), gossip.sender().toString());
            }

            @Override
            public void onMembershipEvent(MembershipEvent event) {
                LOG.info("Membership Event: " + event.toString());
            }
        }).startAwait();

        Flux.interval(Duration.ofSeconds(1))
                .flatMap(v -> Flux.fromIterable(c.otherMembers()))
                .flatMap(member -> c.send(member, Message.fromData(friendlyName + ThreadLocalRandom.current().nextInt(1, 100))))
                .subscribeOn(Schedulers.elastic())
                .blockLast();
    }

    public static class Builder {

        private final Collection<Address> seedAddresses = new HashSet<>();
        private int port;
        private String friendlyName;
        private String syncGroup;

        public Builder seedAddress(String host, int port) {
            this.seedAddresses.add(Address.create(host, port));
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder friendlyName(String name) {
            this.friendlyName = name;
            return this;
        }

        public Builder syncGroup(String groupName) {
            this.syncGroup = groupName;
            return this;
        }

        public Service build() {
            return new Service(seedAddresses, port, friendlyName, syncGroup);
        }
    }
}
