package com.loyer.modules.tools.inherit;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;

/**
 * 自定义FONT处理创建PDF中文异常
 *
 * @author kuangq
 * @date 2020-07-01 22:10
 */
public class AsianFontProvider extends XMLWorkerFontProvider {

    @Override
    public Font getFont(final String fontName, String encoding, float size, final int style) {
        try {
            BaseFont baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            return new Font(baseFont, 12, Font.NORMAL);
        } catch (Exception e) {
            return super.getFont(fontName, encoding, size, style);
        }
    }
}