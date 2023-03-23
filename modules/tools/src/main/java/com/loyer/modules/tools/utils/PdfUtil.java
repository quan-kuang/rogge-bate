package com.loyer.modules.tools.utils;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.loyer.common.core.constant.SuffixConst;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.dedicine.utils.StringUtil;
import com.loyer.modules.tools.inherit.AsianFontProvider;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PDF工具类
 *
 * @author kuangq
 * @date 2020-07-01 21:35
 */
public class PdfUtil {

    //pdf模板文件
    private static final String HTML_SRC = "/static/html/pdf-demo.html";

    //印章图片
    private static final String DZYZ_SRC = "/static/img/dzyz-demo.png";

    //合成pdf文件地址
    private static final String PDF_PATH = SystemConst.LOCAL_FILE_PATH + "pdf/";

    /**
     * 创建PDF
     *
     * @author kuangq
     * @date 2020-07-05 1:24
     */
    public static ApiResult createPdf(Map<String, Object> content) {
        PdfUtil pdfUtil = new PdfUtil();
        //确保文件根目录存在
        File parentFile = new File(PDF_PATH);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        //html文件转String
        String html = pdfUtil.getHtml(HTML_SRC);
        //生成临时文件路径
        String filePath = PDF_PATH + GeneralUtil.getUuid() + SuffixConst.PDF;
        //创建pdf文件
        htmlToPdf(html, filePath);
        //获取图章字节数组
        byte[] imgBytes = pdfUtil.getImgBytes(DZYZ_SRC);
        //添加图章
        addStamp(filePath, filePath, imgBytes, "派出所盖章", 0.3f, 33);
        //获取文件所在服务器的url
        String url = filePath.substring(filePath.indexOf("pdf/"));
        return ApiResult.success(url);
    }

    /**
     * html转pdf
     *
     * @author kuangq
     * @date 2020-07-01 22:19
     */
    @SneakyThrows
    public static void htmlToPdf(String html, String filePath) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        Document document = null;
        try {
            //读取html模板文件、注意字符集编码
            inputStream = new ByteArrayInputStream(StringUtil.decode(html));
            //创建PDF输出流
            outputStream = Files.newOutputStream(Paths.get(filePath));
            //创建文档对象
            document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
            //设置pdf首选项
            pdfWriter.setViewerPreferences(PdfWriter.HideToolbar);
            //打开文档
            document.open();
            //写入pdf内容
            XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
            //引入asian包创建AsianFontProvider处理中文异常
            xmlWorkerHelper.parseXHtml(pdfWriter, document, inputStream, StandardCharsets.UTF_8, new AsianFontProvider());
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                //异常：HintEnum.HINT_1011
            }
        }
    }

    /**
     * pdf添加图章
     *
     * @author kuangq
     * @date 2020-07-05 0:32
     */
    @SneakyThrows
    public static void addStamp(String sourcePath, String targetPath, byte[] imgBytes, String stampGoal, float scale, int offset) {
        InputStream inputStream = null;
        PdfReader pdfReader = null;
        PdfStamper pdfStamper = null;
        try {
            //校验源文件是否存在
            File file = new File(sourcePath);
            if (!file.exists()) {
                throw new BusinessException(HintEnum.HINT_1009, sourcePath);
            }
            //读取需要签章的pdf
            inputStream = Files.newInputStream(file.toPath());
            pdfReader = new PdfReader(inputStream);
            //pdf追加图片的对象
            pdfStamper = new PdfStamper(pdfReader, Files.newOutputStream(Paths.get(targetPath)));
            //根据指定内容获取需要盖图的坐标
            List<float[]> coords = getCoords(pdfReader, stampGoal);
            for (float[] coord : coords) {
                //操作页
                int pageNo = (int) coord[0];
                //图片位置宽度从左下角开始
                float absoluteX = coord[1] + offset;
                //图片位置高度从左下角开始
                float absoluteY = coord[2] - offset;
                //获取需要操作的页面
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNo);
                //读取印章图片
                Image image = Image.getInstance(imgBytes);
                //获取签章图片大小
                float fitWidth = image.getWidth() * scale;
                float fitHeight = image.getHeight() * scale;
                //缩放图片
                image.scaleToFit(fitWidth, fitHeight);
                //签章定位
                image.setAbsolutePosition(absoluteX, absoluteY);
                //添加图片
                pdfContentByte.addImage(image);
            }
        } finally {
            try {
                if (pdfStamper != null) {
                    pdfStamper.close();
                }
                if (pdfReader != null) {
                    pdfReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                //异常：HintEnum.HINT_1011
            }
        }
    }

    /**
     * 获取pdf指定内容的坐标
     *
     * @author kuangq
     * @date 2020-07-05 0:16
     */
    @SneakyThrows
    private static List<float[]> getCoords(PdfReader pdfReader, String stampGoal) {
        List<float[]> coords = new ArrayList<>();
        //遍历整个pdf文件
        PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
        for (int index = 1; index <= pdfReader.getNumberOfPages(); index++) {
            int pageIndex = index;
            pdfReaderContentParser.processContent(index, new RenderListener() {
                @Override
                public void renderText(TextRenderInfo textRenderInfo) {
                    //获取整夜文本内容
                    String text = textRenderInfo.getText();
                    //匹配检索内容
                    if (text.contains(stampGoal)) {
                        //获取定位对象
                        Rectangle2D.Float boundingRectange = textRenderInfo.getBaseline().getBoundingRectange();
                        //记录坐标位置
                        float[] floats = new float[3];
                        floats[0] = pageIndex;
                        floats[1] = boundingRectange.x;
                        floats[2] = boundingRectange.y;
                        coords.add(floats);
                    }
                }

                @Override
                public void renderImage(ImageRenderInfo imageRenderInfo) {
                }

                @Override
                public void beginTextBlock() {
                }

                @Override
                public void endTextBlock() {
                }
            });
        }
        return coords;
    }

    /**
     * 获取html字符串
     *
     * @author kuangq
     * @date 2020-07-02 0:54
     */
    @SneakyThrows
    private String getHtml(String name) {
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
    }

    /**
     * 获取图片的字节数组
     *
     * @author kuangq
     * @date 2020-07-02 0:54
     */
    @SneakyThrows
    private byte[] getImgBytes(String name) {
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return IOUtils.toByteArray(inputStream);
    }
}