package com.socioseer.common.domain.model.post;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.socioseer.common.domain.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post_comments")
public class PostComment extends AbstractEntity {

	@Id
	private String id;
	private String postId;
	private String description;
	private String createdByName;
	private String userId;

}
