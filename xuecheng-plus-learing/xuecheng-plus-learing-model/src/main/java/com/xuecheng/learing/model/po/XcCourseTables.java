package com.xuecheng.learing.model.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("xc_course_tables")
public class XcCourseTables implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 选课记录id
     */
    private Long chooseCourseId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 机构id
     */
    private Long companyId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 添加时间
     */
    private LocalDateTime createDate;

    /**
     * 开始服务时间
     */
    private LocalDateTime validtimeStart;

    /**
     * 到期时间
     */
    private LocalDateTime validtimeEnd;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 备注
     */
    private String remarks;


}
