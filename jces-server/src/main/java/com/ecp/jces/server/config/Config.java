package com.ecp.jces.server.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.ecp.jces.server.util.redislock.DistributedLock;
import com.ecp.jces.server.util.redislock.RedisDistributedLock;
import com.ecp.jces.server.util.sequence.Sequence;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Configuration
@AutoConfigureAfter(CacheConfig.class)
public class Config{

	public static Sequence Generator;

	public static Set<String> DevPermission = new HashSet<>();

	@Bean
	public DistributedLock redisDistributedLock(RedisTemplate<Object, Object> redisTemplate){
		return new RedisDistributedLock(redisTemplate);
	}

	@Bean
	public ServletRegistrationBean<StatViewServlet> druidStatViewServle() {
		ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(
				new StatViewServlet(), "/druid/*");
		// 白名单
		// servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
		// IP黑名单
		// servletRegistrationBean.addInitParameter("deny", "192.168.1.1");
		// 登录查看信息的账号密码.
		servletRegistrationBean.addInitParameter("loginUsername", "kangjunrong");
		servletRegistrationBean.addInitParameter("loginPassword", "qwe2591695");
		// 是否能够重置数据.
		servletRegistrationBean.addInitParameter("resetEnable", "false");
		return servletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<WebStatFilter> druidStatFilter() {

		FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(
				new WebStatFilter());
		// 过滤规则
		filterRegistrationBean.addUrlPatterns("/*");
		// 忽略的信息
		filterRegistrationBean.addInitParameter("exclusions", "/druid/*");
		return filterRegistrationBean;
	}

	@PostConstruct
	public void init() {
		Config.Generator = new Sequence(0L, 0L);

		// 开发者权限通过add方法初始化
		DevPermission.add("/jces-server/applet/page");
		DevPermission.add("/jces-server/applet/add");
		DevPermission.add("/jces-server/applet/edit");
		DevPermission.add("/jces-server/applet/del");
		DevPermission.add("/jces-server/applet/get");
		DevPermission.add("/jces-server/applet/commitCap");
		DevPermission.add("/jces-server/applet/appTest");
		DevPermission.add("/jces-server/appletVersion/get");
		DevPermission.add("/jces-server/applet/testDetail");
		DevPermission.add("/jces-server/applet/testApduDetail");
		DevPermission.add("/jces-server/applet/pdfReport");
		DevPermission.add("/jces-server/testBusinessScript/page");
		DevPermission.add("/jces-server/testBusinessScript/add");
		DevPermission.add("/jces-server/testBusinessScript/commitPretest");
		DevPermission.add("/jces-server/licenceCode/page");
		DevPermission.add("/jces-server/licenceCode/add");
		DevPermission.add("/jces-server/licenceCode/cancel");
		DevPermission.add("/jces-server/licenceCode/edit");
		DevPermission.add("/jces-server/tool/getIDEAndManualPath");
		DevPermission.add("/jces-server/specification/page");
		DevPermission.add("/jces-server/user/resetPassword");
		DevPermission.add("/jces-server/testBusinessScript/getLastVersion");
		DevPermission.add("/jces-server/appletVersion/getCapLastVersion");
		DevPermission.add("/jces-server/user/forum");
		DevPermission.add("/jces-server/testBusinessScript/findByAppletId");
		DevPermission.add("/jces-server/specification/downloadUpdate");
		DevPermission.add("/jces-server/cos/list");
		DevPermission.add("/jces-server/sysConfig/getByLabel");
		DevPermission.add("/jces-server/templateConfig/findById");
		DevPermission.add("/jces-server/applet/pdfReportSave");
		DevPermission.add("/jces-server/testBusinessScript/del");
		DevPermission.add("/jces-server/testTask/findByTestBusinessScriptId");
	}
}
