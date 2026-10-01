package avatar.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import avatar.model.Position;

public class DataParser {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static List<Position> parsePositions(String json) throws JsonProcessingException {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(mapper.readValue(json, Position[].class)));
    }

    public static String toJsonPositions(List<Position> positions) throws JsonProcessingException {
        if (positions == null) {
            return "[]";
        }
        return mapper.writeValueAsString(positions);
    }
}
