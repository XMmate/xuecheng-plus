package com.xuecheng.learing.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learing.feignclient.ContentServiceClient;
import com.xuecheng.learing.mapper.XcChooseCourseMapper;
import com.xuecheng.learing.mapper.XcCourseTablesMapper;
import com.xuecheng.learing.model.dto.MyCourseTableParams;
import com.xuecheng.learing.model.dto.XcChooseCourseDto;
import com.xuecheng.learing.model.dto.XcCourseTablesDto;
import com.xuecheng.learing.model.po.XcChooseCourse;
import com.xuecheng.learing.model.po.XcCourseTables;
import com.xuecheng.learing.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;



import org.springframework.stereotype.Service;
@Service
@Slf4j
public class MyCourseTablesServiceImpl implements MyCourseTablesService {
    @Autowired
    XcChooseCourseMapper xcChooseCourseMapper;

    @Autowired
    XcChooseCourseMapper chooseCourseMapper;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    XcCourseTablesMapper xcCourseTablesMapper;
    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        //课程收费标准
        String charge = coursepublish.getCharge();
        //选课记录
        XcChooseCourse chooseCourse = null;
        if("201000".equals(charge)){//课程免费
            //添加免费课程
            chooseCourse  = addFreeCoruse(userId, coursepublish);
            //添加到我的课程表
            XcCourseTables xcCourseTables = addCourseTabls(chooseCourse);
            xcChooseCourseDto.setLearnStatus("702001");
        }else{
            //添加收费课程
            chooseCourse  = addChargeCoruse(userId, coursepublish);
            xcChooseCourseDto.setLearnStatus("702002");
        }
        BeanUtils.copyProperties(chooseCourse,xcChooseCourseDto);

        return xcChooseCourseDto;
    }

    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        //查询我的课程表
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if(xcCourseTables==null){
            XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
            //没有选课或选课后没有支付
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
        BeanUtils.copyProperties(xcCourseTables,xcCourseTablesDto);
        //是否过期,true过期，false未过期
        boolean isExpires = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if(!isExpires){
            //正常学习
            xcCourseTablesDto.setLearnStatus("702001");
            return xcCourseTablesDto;

        }else{
            //已过期
            xcCourseTablesDto.setLearnStatus("702003");
            return xcCourseTablesDto;
        }
    }

    //添加选课
    @Override
    public boolean saveChooseCourseSuccess(String chooseCourseId) {
        //根据choosecourseId查询选课记录
        XcChooseCourse xcChooseCourse = chooseCourseMapper.selectById(chooseCourseId);
        if(xcChooseCourse == null){
            log.debug("收到支付结果通知没有查询到关联的选课记录,choosecourseId:{}",chooseCourseId);
            return false;
        }
        String status = xcChooseCourse.getStatus();
        if("701001".equals(status)){
            //添加到课程表
            addCourseTabls(xcChooseCourse);
            return true;
        }
        //待支付状态才处理
        if ("701002".equals(status)) {
            //更新为选课成功
            xcChooseCourse.setStatus("701001");
            int update = chooseCourseMapper.updateById(xcChooseCourse);
            if(update>0){
                log.debug("收到支付结果通知处理成功,选课记录:{}",xcChooseCourse);
                //添加到课程表
                addCourseTabls(xcChooseCourse);
                return true;
            }else{
                log.debug("收到支付结果通知处理失败,选课记录:{}",xcChooseCourse);
                return false;
            }
        }

        return false;
    }



    /**
     * 查询我的课程表
     * @param userId
     * @param courseId
     * @return
     */
    private XcCourseTables getXcCourseTables(String userId, Long courseId) {
       return xcCourseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getCourseId, courseId).eq(XcCourseTables::getUserId, userId));

    }


    //添加免费课程,免费课程加入选课记录表、我的课程表
    public XcChooseCourse addFreeCoruse(String userId, CoursePublish coursepublish) {
        System.out.println("添加免费课程");
       //查询选课记录表是否存在免费的且选课成功的订单
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursepublish.getId())
                .eq(XcChooseCourse::getOrderType, "700001")//免费课程
                .eq(XcChooseCourse::getStatus, "701001");//选课成功
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && xcChooseCourses.size()>0) {
            return xcChooseCourses.get(0);
        }
        //添加选课记录信息
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursepublish.getId());
        xcChooseCourse.setCourseName(coursepublish.getName());
        xcChooseCourse.setCoursePrice(0f);//免费课程价格为0
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursepublish.getCompanyId());
        xcChooseCourse.setOrderType("700001");//免费课程
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        xcChooseCourse.setStatus("701001");//选课成功

        xcChooseCourse.setValidDays(365);//免费课程默认365
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        xcChooseCourseMapper.insert(xcChooseCourse);

        return xcChooseCourse;
    }

    //添加收费课程
    public XcChooseCourse addChargeCoruse(String userId,CoursePublish coursepublish){
//如果存在待支付记录直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursepublish.getId())
                .eq(XcChooseCourse::getOrderType, "700002")//收费订单
                .eq(XcChooseCourse::getStatus, "701002");//待支付
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && xcChooseCourses.size()>0) {
            return xcChooseCourses.get(0);
        }

        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursepublish.getId());
        xcChooseCourse.setCourseName(coursepublish.getName());
        xcChooseCourse.setCoursePrice(coursepublish.getPrice());
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursepublish.getCompanyId());
        xcChooseCourse.setOrderType("700002");//收费课程
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        xcChooseCourse.setStatus("701002");//待支付

        xcChooseCourse.setValidDays(coursepublish.getValidDays());
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        xcChooseCourseMapper.insert(xcChooseCourse);
        return xcChooseCourse;
    }

    //添加到我的课程表
    public XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse){
        //选课记录完成且未过期可以添加课程到课程表
        String status = xcChooseCourse.getStatus();
        if (!"701001".equals(status)){
            XueChengPlusException.cast("选课未成功，无法添加到课程表");
        }
        //查询我的课程表
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if(xcCourseTables!=null){
            return xcCourseTables;
        }
        XcCourseTables xcCourseTablesNew = new XcCourseTables();
        xcCourseTablesNew.setChooseCourseId(xcChooseCourse.getId());
        xcCourseTablesNew.setUserId(xcChooseCourse.getUserId());
        xcCourseTablesNew.setCourseId(xcChooseCourse.getCourseId());
        xcCourseTablesNew.setCompanyId(xcChooseCourse.getCompanyId());
        xcCourseTablesNew.setCourseName(xcChooseCourse.getCourseName());
        xcCourseTablesNew.setCreateDate(LocalDateTime.now());
        xcCourseTablesNew.setValidtimeStart(xcChooseCourse.getValidtimeStart());
        xcCourseTablesNew.setValidtimeEnd(xcChooseCourse.getValidtimeEnd());
        xcCourseTablesNew.setCourseType(xcChooseCourse.getOrderType());
        xcCourseTablesMapper.insert(xcCourseTablesNew);
        return xcCourseTablesNew;

    }

    /**
     * 我的课程表
     */
    @Override
    public PageResult<XcCourseTables> mycoursetables(MyCourseTableParams params) {
        //页码
        long pageNo = params.getPage();
        //每页记录数
        long pageSize = params.getSize();
        //分页条件
        Page<XcCourseTables> page = new Page<>(pageNo, pageSize);
        //根据用户id查询
        String userId = params.getUserId();
        LambdaQueryWrapper<XcCourseTables> lambdaQueryWrapper = new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId);

        //分页查询
        Page<XcCourseTables> pageResult = xcCourseTablesMapper.selectPage(page, lambdaQueryWrapper);
        List<XcCourseTables> records = pageResult.getRecords();
        //记录总数
        long total = pageResult.getTotal();
        PageResult<XcCourseTables> courseTablesResult = new PageResult<>(records, total, pageNo, pageSize);
        return courseTablesResult;

    }


}



