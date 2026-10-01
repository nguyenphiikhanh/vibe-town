package avatar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ImageInfo {
    
    private int id;
    private int bigImageID;
    private int x;
    private int y;
    private int w;
    private int h;
}
