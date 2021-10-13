package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.domain.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.util.Random;

import static com.woowacourse.zzimkkong.Constants.MAP_IMAGE_URL;
import static com.woowacourse.zzimkkong.Constants.MAP_SVG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
class ThumbnailManagerImplTest {
    @Autowired
    ThumbnailManagerImpl thumbnailManager;

    @MockBean
    SvgConverter svgConverter;

    @MockBean
    StorageUploader storageUploader;

    @Test
    @DisplayName("Map의 svg 데이터와 Map을 받고 썸네일의 url을 받아온다.")
    void uploadMapThumbnailInmemory() {
        // given
        Map mockMap = mock(Map.class);
        long mapId = new Random().nextLong();
        given(mockMap.getId())
                .willReturn(mapId);

        given(storageUploader.upload(anyString(), anyString(), any(InputStream.class)))
                .willReturn(MAP_IMAGE_URL);

        // when
        String mapThumbnailUrl = thumbnailManager.uploadMapThumbnail(MAP_SVG, mockMap);

        assertThat(mapThumbnailUrl).isEqualTo(MAP_IMAGE_URL);
    }
}
