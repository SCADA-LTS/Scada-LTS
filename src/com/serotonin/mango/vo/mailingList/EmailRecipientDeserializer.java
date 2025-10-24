package com.serotonin.mango.vo.mailingList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.api.dto.AddressEntryJson;
import org.scada_lts.web.mvc.api.dto.EmailRecipientJson;
import org.scada_lts.web.mvc.api.dto.MailingListJson;
import org.scada_lts.web.mvc.api.dto.UserEntryJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EmailRecipientDeserializer extends JsonDeserializer<List<EmailRecipientJson>>
{
    private ObjectMapper objectMapper;

    public EmailRecipientDeserializer() {
        this.objectMapper = ApplicationBeans.getObjectMapper();
    }

    @Override
    public List<EmailRecipientJson> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException
    {
        List<EmailRecipientJson> listOfRecipients = new ArrayList<>();
        ObjectCodec oc = jsonParser.getCodec();
        ArrayNode recipientListNode = oc.readTree(jsonParser);
        Iterator<JsonNode> entries = recipientListNode.elements();
        while (entries.hasNext()) {
            JsonNode entryNode = entries.next();
            int recipientType = entryNode.get("recipientType").asInt();

            EmailRecipientJson recipient = null;
            if (recipientType == EmailRecipient.TYPE_MAILING_LIST) {
                recipient = objectMapper.readValue(entryNode.toString(), MailingListJson.class);
            } else if (recipientType == EmailRecipient.TYPE_USER)
            {
                recipient = objectMapper.readValue(entryNode.toString(), UserEntryJson.class);
            }
            else if (recipientType == EmailRecipient.TYPE_ADDRESS)
            {
                recipient = objectMapper.readValue(entryNode.toString(), AddressEntryJson.class);
            }

            listOfRecipients.add(recipient);
        }

        return listOfRecipients;
    }
}