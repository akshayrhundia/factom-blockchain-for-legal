package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    Map<String, Object> data;
    Map<String, Object> meta;
    String legal;
}
