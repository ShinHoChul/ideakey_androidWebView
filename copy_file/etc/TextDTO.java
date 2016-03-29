package copytestapp.copytestapp.etc;

/**
 * Created by Y on 2015-06-22.
 */
public class TextDTO
{
    private String title;
    private String content;
    private String picturUrl;
    private String linkUrl;
    private int adcodeNum;//DB power_advertiser Table에 있는 code을 말하는것임
    private String adNum;//DB power_advertiser Table에 있는 num을 말하는것임

    public void setAdNum(String adNum) {
        this.adNum = adNum;
    }

    public void setAdcodeNum(int adcodeNum) {
        this.adcodeNum = adcodeNum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPicturUrl(String picturUrl) {
        this.picturUrl = picturUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPicturUrl() {
        return picturUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public int getAdcodeNum() {
        return adcodeNum;
    }

    public String getAdNum() {
        return adNum;
    }
}
