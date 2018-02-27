package com.lewis.lib_vinci.service.db;



import java.io.Serializable;

public class AccountModel implements Serializable{

	/**
	 * 类说明：用户信息
	 * 
	 * @date 2015/05/22
	 * @author Lihailun
	 */

	// 员工信息（employ）
	// 4S店名称 fsAbbrname varchar(50) Y
	// 4S店ID empFs varchar(50) Y
	// 员工ID empId varchar(32) Y
	// 员工姓名 empName varchar(20) Y
	// 员工职位 empPost varchar(20) Y
	// 员工图片 empPic varchar(200) Y
	// 员工电话 empPhone varchar(20) Y
	// 权限分类 empPower varchar(5) Y 1-一级权限，2-二级权限
	// 总积分 empGrade Int Y
	// 已兑换积分 empDrawgrade Int Y
	// 服务区域 areaValue Varchar(50) Y 多个区域之间以“#”分割
	// 4S店照片 fsPic Varchar(50) Y
	// 4S店电话 fsTel Varchar(50) Y
	// 4S店地址 fsAddress Varchar(50) Y [V2.0新增]
	//

	public static final String TABLE_NAME = "account_table_name";
	public static final String CREATE_TABLE_SQL = "create table "
			+ TABLE_NAME
			+ "( id integer PRIMARY KEY AUTOINCREMENT , fsAbbrname char, empFs char, empId char, empName char , empPost char, empPic char , empPhone char, empPower char ,logintime long,empGrade char,empDrawgrade char,areaValue char,fsPic char,fsTel char,fsAddress char)";

	private int id;
	private String fsAbbrname;
	private String empFs;
	private String empId;
	private String empName;
	private String empPost;
	private String empPic;
	private String empPhone;
	private String empPower;
	private long logintime;
	private String empGrade;
	private String empDrawgrade;
	private String areaValue;
	private String fsPic;
	private String fsTel;
	private String fsAddress;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getLogintime() {
		return logintime;
	}

	public void setLogintime(long logintime) {
		this.logintime = logintime;
	}

	public String getEmpFs() {
		return empFs;
	}

	public void setEmpFs(String empFs) {
		this.empFs = empFs;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpPost() {
		return empPost;
	}

	public void setEmpPost(String empPost) {
		this.empPost = empPost;
	}

	public String getEmpPic() {
		return empPic;
	}

	public void setEmpPic(String empPic) {
		this.empPic = empPic;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getEmpPower() {
		return empPower;
	}

	public void setEmpPower(String empPower) {
		this.empPower = empPower;
	}

	public String getFsAbbrname() {
		return fsAbbrname;
	}

	public void setFsAbbrname(String fsAbbrname) {
		this.fsAbbrname = fsAbbrname;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getEmpDrawgrade() {
		return empDrawgrade;
	}

	public void setEmpDrawgrade(String empDrawgrade) {
		this.empDrawgrade = empDrawgrade;
	}

	public String getAreaValue() {
		return areaValue;
	}

	public void setAreaValue(String areaValue) {
		this.areaValue = areaValue;
	}

	public String getFsPic() {
		return fsPic;
	}

	public void setFsPic(String fsPic) {
		this.fsPic = fsPic;
	}

	public String getFsTel() {
		return fsTel;
	}

	public void setFsTel(String fsTel) {
		this.fsTel = fsTel;
	}

	public String getFsAddress() {
		return fsAddress;
	}

	public void setFsAddress(String fsAddress) {
		this.fsAddress = fsAddress;
	}

}
