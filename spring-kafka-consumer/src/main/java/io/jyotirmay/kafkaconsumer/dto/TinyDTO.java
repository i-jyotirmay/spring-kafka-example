package io.jyotirmay.kafkaconsumer.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TinyDTO implements Serializable {

    private String tinyID;

    private String tinyName;

    private String tinyDescription;
}
