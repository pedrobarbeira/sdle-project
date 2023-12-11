import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.operation.*;
import org.sdle.server.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class CRDTOpTest {

    @Test
    void classHierarchySerializationTest() throws JsonProcessingException {
        CRDTOpItemsCreate op = new CRDTOpItemsCreate("test", "test", 0);
        ObjectMapper mapper = ObjectFactory.getMapper();
        String jsonString = mapper.writeValueAsString(op);
        CRDTOp<ShoppingList> operation = mapper.readValue(jsonString, new TypeReference<>() {});
        operation.apply(null);
    }
}
