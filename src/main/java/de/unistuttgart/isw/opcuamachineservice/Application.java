package de.unistuttgart.isw.opcuamachineservice;

import com.siemens.ct.exi.exceptions.EXIException;
import de.unistuttgart.isw.opcuamachineservice.opcua.Subscriptions;
import de.unistuttgart.isw.opcuamachineservice.utils.Json2NodeId;

import de.unistuttgart.isw.opcuamachineservice.utils.XMLHelper;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageBus;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageSender;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Timur Tasci
 * @version 1.0
 * @date 11.08.16
 * @copyright Timur Tasci, ISW Uni Stuttgart
 */
public class Application {
    public static void main(String[] args) throws Exception {
        System.setProperty("http.agent", "Mozilla/5.0");
        Subscriptions subscriptions = new Subscriptions();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        NodeId[] ids = Json2NodeId.json2NodeId(new File(classLoader.getResource("subscribeItems.json").getFile()));
        List<UaMonitoredItem> itemList = subscriptions.createMonitoredItemsFromNodes(ids).get();
        MessageBus bus = new MessageBus();
        MessageSender sender = bus.createSender("OUTPUT_TestTopic", "https://opcfoundation.org/UA/2008/02/Types.xsd", false);
        for (UaMonitoredItem item : itemList){
            item.setValueConsumer(v -> {
                try {
                    sender.send(XMLHelper.createXML(v));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (EXIException e) {
                    e.printStackTrace();
                }
                System.out.println(v.getValue());
                System.out.println(v.getServerTime());
            });
        }

        bus.createReceiver("OUTPUT_TestTopic", "https://opcfoundation.org/UA/2008/02/Types.xsd", xml -> {
            System.out.println(xml);
        }, false);

        bus.runListener();
    }
}
