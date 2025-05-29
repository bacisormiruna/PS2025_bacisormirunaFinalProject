package com.example.demo.dto.responsedto;

import com.example.demo.enumeration.RequestStatus;
import lombok.Data;

@Data
public class ResponseDTO {
   private RequestStatus status;
}
