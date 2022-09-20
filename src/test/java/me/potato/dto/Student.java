package me.potato.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    private String        name;
    private int           age;
    private String        city;
    private List<Integer> marks;
}
