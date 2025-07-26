package com.yitong.base.api.ocr.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OCR识别响应DTO
 */
@Data
public class OcrRecognitionResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 识别结果文本
     */
    private String text;
    
    /**
     * 识别结果详情
     */
    private List<OcrTextBlock> textBlocks;
    
    /**
     * 识别置信度
     */
    private Double confidence;
    
    /**
     * 识别耗时（毫秒）
     */
    private Long duration;
    
    @Data
    public static class OcrTextBlock implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 文本内容
         */
        private String text;
        
        /**
         * 置信度
         */
        private Double confidence;
        
        /**
         * 位置信息
         */
        private Position position;
        
        @Data
        public static class Position implements Serializable {
            
            private static final long serialVersionUID = 1L;
            
            /**
             * 左上角X坐标
             */
            private Integer x;
            
            /**
             * 左上角Y坐标
             */
            private Integer y;
            
            /**
             * 宽度
             */
            private Integer width;
            
            /**
             * 高度
             */
            private Integer height;
        }
    }
}