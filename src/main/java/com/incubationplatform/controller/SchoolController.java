package com.incubationplatform.controller;

import com.incubationplatform.common.ServerResponse;
import com.incubationplatform.pojo.Admin;
import com.incubationplatform.service.IAdminService;
import com.incubationplatform.service.IProjectService;
import com.incubationplatform.vo.ProjectExcelVo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liaochaofan
 * @date 2019/3/5 20:36
 */
@Controller
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private IProjectService iProjectService;
    @Autowired
    private IAdminService adminService;


    /**
     * 学校端审核接口
     * @param productId
     * @param adminId
     * @param opinion
     * @param status
     * @return
     */
    @RequestMapping("/{adminId}/save_review_status")
    @ResponseBody
    public ServerResponse reviewProject(Integer productId, @PathVariable Integer adminId, String opinion, Integer status){
        return iProjectService.reviewProject(productId, adminId, opinion, status);
    }

    @RequestMapping("/{adminId}/update_admin")
    @ResponseBody
    public ServerResponse updateAdmin(@PathVariable Integer adminId, Admin admin){
        return null;
    }

    @RequestMapping("/{adminId}/add_admin")
    @ResponseBody
    public ServerResponse addAdmin(Admin admin){
        return adminService.addAdmin(admin);
    }

    @RequestMapping("/{adminId}/delete_admin")
    @ResponseBody
    public ServerResponse deleteAdmin(Admin admin){
        return adminService.removeAdmin(admin);
    }

    @RequestMapping("/{adminId}/get_admin")
    @ResponseBody
    public ServerResponse getAdmin(@RequestParam(required = false) Integer page,@RequestParam(required = false) String status){
        return adminService.getAdmin(page,status);
    }

    @RequestMapping("/{adminId}/query_admin")
    public ServerResponse queryAdmin(Integer page,Admin admin){
        return adminService.queryAdmin(admin,page);
    }


    /**
     * 按状态分页获取项目
     * @param page
     * @param status
     * @return
     */
    @RequestMapping("/{adminId}/get_project")
    public ServerResponse getProject(Integer page,Integer status){
        return iProjectService.getProjectByStatus(page, status);
    }


    @RequestMapping("/project/create_excel")
    public void createExcel(HttpServletResponse response) throws IOException {
        SimpleDateFormat sdforYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取我们需要转化成Excel表格的数据，在这里我转化成JSONArray格式的数据
        //JSONArray jsonArray = JSON.parseArray( resultJsonObject.getString("data"));
        //logger.info("放入excel表格当中的数据是："+jsonArray.toJSONString());
        //excel文件的头信息我们一般情况下是知道的，所以在这里定义一个数组
        Map<String,String> kv = new LinkedHashMap<>();
        kv.put("serialNumber","序号");
        kv.put("name","项目名称");
        kv.put("classification","类型");
        kv.put("mainName","负责人");
        kv.put("mainNum","负责人学号");
        kv.put("mainClass","负责人专业班级");
        kv.put("mainPhotoNum","负责人联系电话");
        kv.put("mainQQ","负责人qq号");
        kv.put("membership","参与学生总人数");
        kv.put("otherName","项目其他成员信息");
        kv.put("teacher","指导老师");
        kv.put("post","指导老师职称");
        kv.put("subjectCode","项目所属一级学科代码");
        kv.put("introduce","项目简介");
        kv.put("grade","推荐等级");

        int i = 0;
        //创建HSSFSheet对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //设置excel文件的名称
        HSSFSheet sheet = wb.createSheet("奖品发放数据");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        for(String key : kv.keySet()){
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(kv.get(key));
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
            //设置指定列的列宽，256 * 50这种写法是因为width参数单位是单个字符的256分之一
            sheet.setColumnWidth(cell.getColumnIndex(), 100 * 50);
            i++;
        }
        String[] excelHeader = {"序号", "奖项类型", "奖项内容","已发数量", "库存数量","是否特殊概率奖项"};
        List<ProjectExcelVo> projectExcelVoList = new ArrayList<>();
        for(int j = 0 ;j < 10 ; j++){
            ProjectExcelVo projectExcelVo = new ProjectExcelVo();
        }
        i=0;
        for (ProjectExcelVo projectExcelVo : projectExcelVoList){
            row = sheet.createRow(i + 1);
            //JSONObject jsonObject = jsonArray.getJSONObject(i);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(projectExcelVo.getName());
            row.createCell(2).setCellValue(projectExcelVo.getClassification());
            row.createCell(3).setCellValue(projectExcelVo.getMainName());
            row.createCell(4).setCellValue(projectExcelVo.getMainNum());
            row.createCell(5).setCellValue(projectExcelVo.getMainClass());
            row.createCell(6).setCellValue(projectExcelVo.getMainPhoneNum());
            row.createCell(7).setCellValue(projectExcelVo.getMainQQ());
            row.createCell(8).setCellValue(projectExcelVo.getMembership());
            row.createCell(9).setCellValue(projectExcelVo.getOtherName());
            row.createCell(10).setCellValue(projectExcelVo.getTeacher());
            row.createCell(11).setCellValue(projectExcelVo.getPost());
            row.createCell(12).setCellValue(projectExcelVo.getSubjectCode());
            row.createCell(13).setCellValue(projectExcelVo.getIntroduce());
            row.createCell(14).setCellValue(projectExcelVo.getGrade());

        }

        //返回给前端信息
        OutputStream output = response.getOutputStream();
        response.addHeader("Content-Disposition", "inline;filename=" + sdforYMDHMS.format(new Date()) + ".xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.flush();
        output.close();

    }
}
