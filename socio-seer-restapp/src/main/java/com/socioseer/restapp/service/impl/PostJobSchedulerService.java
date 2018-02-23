package com.socioseer.restapp.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.campaign.enums.MediaType;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.dto.MediaDto;
import com.socioseer.common.dto.PostDto;
import com.socioseer.common.dto.PostScheduleDto;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>PostJobScheduler Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service
public class PostJobSchedulerService {

  @Value("${socio.seer.media.location}")
  private String mediaLocation;

  @Autowired
  private MediaService mediaService;

  @Autowired
  private UrlUtil urlUtil;

  private final String MEDIA_LOCATION_FORMAT = "%s/%s/";

  private final String FILE_FORMAT = "%s/%s/%s";

  /**
   * <b>Create Post Schedule list</b>
   * @param postSchedules
   * @return	returns PostScheduleDto list
   */
  public List<PostScheduleDto> createPostScheduleDtoList(List<PostSchedule> postSchedules) {
    return postSchedules.stream().flatMap(postSchedule -> {
      return createPostScheduleDtoList(postSchedule).stream();
    }).collect(Collectors.toList());
  }

  /**
   * <b>Create Post Schedule list</b>
   * @param postSchedule
   * @return	returns PostScheduleDto
   */
  private List<PostScheduleDto> createPostScheduleDtoList(PostSchedule postSchedule) {
    Post post = postSchedule.getPost();
    final List<MediaDto> mediaDtos = createMediaDto(post.getClientId(), post.getMediaIds());
    final PostDto postDto = createPostDto(post, mediaDtos);
    return postSchedule.getPostHandlers().stream().map(handler -> {
      return createPostSchedule(postSchedule.getId(), postDto, postSchedule.getPlatform().getName(),
          handler.getHandlerId());
    }).collect(Collectors.toList());
  }

  /**
   * <b>Create Post Schedule</b>
   * @param id
   * @param postDto
   * @param platform
   * @param handlerId
   * @return	returns PostScheduleDto
   */
  private PostScheduleDto createPostSchedule(String id, PostDto postDto, String platform,
      String handlerId) {
    return new PostScheduleDto(id, postDto, platform, handlerId);
  }

  private List<MediaDto> createMediaDto(String clientId, List<String> mediaIds) {

    if (CollectionUtils.isEmpty(mediaIds)) {
      return Collections.emptyList();
    }

    String uploadFolder = String.format(MEDIA_LOCATION_FORMAT, mediaLocation, clientId);
    return mediaIds.stream().map(id -> {
      Media media = mediaService.get(id);
      if (ObjectUtils.isEmpty(media)) {
        return null;
      }
      String fileLocation = StringUtils.EMPTY;
      if (StringUtils.isNotEmpty(media.getVideoUrl())) {
        return new MediaDto(id, StringUtils.EMPTY, StringUtils.EMPTY, media.getClientId(),
            MediaType.URL.name(), StringUtils.EMPTY, media.getVideoUrl(), StringUtils.EMPTY);
      }
      fileLocation =
          String.format(FILE_FORMAT, uploadFolder, media.getMediaType(), media.getHashFileName());
      String mediaFileURL = urlUtil.getUrl(fileLocation, media.getHashFileName());
      return new MediaDto(id, media.getOriginalFileName(), media.getHashFileName(),
          media.getClientId(), media.getMediaType().name(), fileLocation, StringUtils.EMPTY,
          mediaFileURL);
    }).filter(media -> media != null).collect(Collectors.toList());
  }

  /**
   * <b>Create PostDto</b>
   * @param post
   * @param mediaDtos
   * @return	returns PostDto
   */
  private PostDto createPostDto(Post post, List<MediaDto> mediaDtos) {
    PostDto postDto = new PostDto();
    postDto.setId(post.getId());
    postDto.setCampaignId(post.getCampaignId());
    postDto.setClientId(post.getClientId());
    postDto.setText(post.getText());
    postDto.setUrl(post.getUrl());
    postDto.setMediaList(mediaDtos);
    return postDto;
  }
}
