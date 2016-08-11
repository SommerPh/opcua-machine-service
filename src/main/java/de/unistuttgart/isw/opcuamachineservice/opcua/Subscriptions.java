package de.unistuttgart.isw.opcuamachineservice.opcua;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 *
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class Subscriptions {

    private final AtomicLong clientHandles = new AtomicLong(1L);

    public Subscriptions(){

    }

    public CompletableFuture<List<UaMonitoredItem>> createMonitoredItemsFromNodes(NodeId[] nodeIds) throws Exception{
        UaSubscription subscription = ClientManager.getInstance().getClient().getSubscriptionManager().createSubscription(10.0).get();
        // client handle must be unique per item
        UInteger clientHandle;
        List<MonitoredItemCreateRequest> request = new ArrayList<>();
        for (int i = 0; i < nodeIds.length; i++){
            clientHandle = uint(clientHandles.getAndIncrement());
            ReadValueId readValueId = new ReadValueId(
                    nodeIds[i],
                    AttributeId.Value.uid(),
                    null,
                    QualifiedName.NULL_VALUE);
            MonitoringParameters parameters = new MonitoringParameters(
                    clientHandle,
                    10.0,     // sampling interval
                    null,       // filter, null means use default
                    uint(10),   // queue size
                    true);       // discard oldest
            request.add(new MonitoredItemCreateRequest(
                    readValueId, MonitoringMode.Reporting, parameters));

        }
        return subscription.createMonitoredItems(TimestampsToReturn.Both, request);
    }
}
