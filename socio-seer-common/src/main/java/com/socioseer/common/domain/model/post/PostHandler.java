package com.socioseer.common.domain.model.post;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostHandler implements Serializable {

  private static final long serialVersionUID = -7433106651737980562L;

  private String handlerId;
  private int status;
  private String message;
  @Indexed
  private List<String> socialPostIds;

  public PostHandler(String handlerId) {
    this.handlerId = handlerId;
  }
}
