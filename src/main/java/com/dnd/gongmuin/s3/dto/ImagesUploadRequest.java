import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ImagesUploadRequest {

	@NotNull(message = "이미지 파일은 필수 입력 항목입니다.")
	@Size(min = 1, max = 10, message = "이미지는 1장 이상 10장 이하로 선택하세요.")
	private List<MultipartFile> imageFiles;

	public List<MultipartFile> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<MultipartFile> imageFiles) {
		this.imageFiles = imageFiles;
	}
}