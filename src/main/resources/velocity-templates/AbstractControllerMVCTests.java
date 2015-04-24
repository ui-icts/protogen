package ${packageName};

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import ${daoPackageName}.${daoServiceClassName};

/**
 * Generated by Protogen
 * @since ${date}
 */
@Transactional
@WebAppConfiguration
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( { "classpath:spring/dispatcher-servlet.xml" } )
public abstract class AbstractControllerMVCTests {
	
	protected final Log log = LogFactory.getLog( getClass() );
	
	@Autowired
	protected ${daoServiceClassName} ${daoServiceName};
	
	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;
	
	@Before
	public void setUp() {
		
		log.debug( "setting up mock MVC" );
		this.mockMvc = MockMvcBuilders.webAppContextSetup( this.wac ).build();

		log.debug( "setting authentication" );
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add( new SimpleGrantedAuthority( "ROLE_ICTS-DEVELOPERS" ) );
		SecurityContextHolder.getContext().setAuthentication( new UsernamePasswordAuthenticationToken( "adminUser", "[PROTECTED]", grantedAuthorities ) );
		
	}
	
}