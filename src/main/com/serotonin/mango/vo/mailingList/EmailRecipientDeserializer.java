package com.serotonin.mango.vo.mailingList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EmailRecipientDeserializer extends JsonDeserializer<List<EmailRecipient>>
{
    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<EmailRecipient> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException
    {
        List<EmailRecipient> listOfRecipients = new ArrayList<EmailRecipient>();
        ObjectCodec oc = jsonParser.getCodec();
        ArrayNode recipientListNode = oc.readTree(jsonParser);
        Iterator<JsonNode> entries = recipientListNode.elements();
        while (entries.hasNext()) {
            JsonNode entryNode = entries.next();
            int recipientType = entryNode.get("recipientType").asInt();

            EmailRecipient recipient = null;
            if (recipientType == EmailRecipient.TYPE_MAILING_LIST) {
                recipient = mapper.readValue(entryNode.toString(), MailingList.class);
            } else if (recipientType == EmailRecipient.TYPE_USER)
            {
                recipient = mapper.readValue(entryNode.toString(), UserEntry.class);
            }
            else if (recipientType == EmailRecipient.TYPE_ADDRESS)
            {
                recipient = mapper.readValue(entryNode.toString(), AddressEntry.class);
            }

            listOfRecipients.add(recipient);
        }

        return listOfRecipients;
    }
}