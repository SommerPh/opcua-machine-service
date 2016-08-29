package de.unistuttgart.isw.opcuamachineservice.utils;

import de.unistuttgart.isw.serviceorchestration.servicecore.MessageBus;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageSender;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timur Tasci
 * @version 1.0
 * @date 29.08.2016
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class KafkaHelper {

    public static Map<String,MessageSender> creadeSendersFromIds(NodeId[] ids, MessageBus bus){
        Map<String,MessageSender> messageSenders = new HashMap<>();
        for (NodeId id : ids){
            messageSenders.put(id.getIdentifier().toString(), bus.createSender(id.getIdentifier().toString().replace(",","."), "https://opcfoundation.org/UA/2008/02/Types.xsd", false));
        }
        return messageSenders;
    }
}
