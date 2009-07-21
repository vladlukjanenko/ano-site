package net.anotheria.anosite.api.generic.login;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.anotheria.anosite.api.common.APIException;
import net.anotheria.anosite.api.common.APIFinder;
import net.anotheria.anosite.api.common.AbstractAPIImpl;
import net.anotheria.anosite.api.common.NoLoggedInUserException;
import net.anotheria.anosite.api.generic.login.processors.SessionCleanupOnLogoutProcessor;
import net.anotheria.anosite.api.generic.observation.ObservationAPI;
import net.anotheria.anosite.api.generic.observation.ObservationSubjects;
import net.anotheria.anosite.api.session.APISessionImpl;
import net.anotheria.util.StringUtils;

/**
 * An implementation for the login api.
 * @author lrosenberg
 *
 */
public class LoginAPIImpl extends AbstractAPIImpl implements LoginAPI{
	
	/**
	 * Login preprocessors. Each of them get called before each login. Can cancel a login.
	 */
	private List<LoginPreProcessor>  loginPreProcessors;
	/**
	 * Login postprocessors.
	 */
	private List<LoginPostProcessor> loginPostProcessors;
	/**
	 * Logout preprocessors.
	 */
	private List<LogoutPreProcessor>  logoutPreProcessors;
	/**
	 * Logout postprocessors.
	 */
	private List<LogoutPostProcessor> logoutPostProcessors;
	/**
	 * Link to the ObservationAPI. Used to announce changes in user logged in state.
	 */
	private ObservationAPI observationAPI;
	
	@Override public void init(){
		super.init();
		
		loginPreProcessors = new CopyOnWriteArrayList<LoginPreProcessor>();
		loginPostProcessors = new CopyOnWriteArrayList<LoginPostProcessor>();
		
		logoutPreProcessors = new CopyOnWriteArrayList<LogoutPreProcessor>();
		logoutPostProcessors = new CopyOnWriteArrayList<LogoutPostProcessor>();
		
		addLogoutPostprocessor(new SessionCleanupOnLogoutProcessor());
		
		observationAPI = APIFinder.findAPI(ObservationAPI.class);
		
	}

	/**
	 * Adds a login postprocessor. Threadsafe.
	 */
	public void addLoginPostprocessor(LoginPostProcessor postProcessor) {
		loginPostProcessors.add(postProcessor);
		
	}

	/**
	 * Adds a login preprocessor. Threadsafe.
	 */
	public void addLoginPreprocessor(LoginPreProcessor preProcessor) {
		loginPreProcessors.add(preProcessor);
		
	}

	@Override public void logInUser(String userId) throws APIException {
		callLoginPreprocessors(userId);
		
		((APISessionImpl)getSession()).setCurrentUserId(userId);
		getCallContext().setCurrentUserId(userId);
		
		callLoginPostprocessors(userId);
		
		observationAPI.fireSubjectUpdateForCurrentUser(ObservationSubjects.LOGIN, this.getClass().getName());
	}

	@Override public void logoutMe() throws APIException {
		try{
			String userId = getCallContext().getCurrentUserId();
			callLogoutPreprocessors(userId);
			
			((APISessionImpl)getSession()).setCurrentUserId(null);
			getCallContext().setCurrentUserId(null);
			
			callLogoutPostprocessors(userId);
			
			observationAPI.fireSubjectUpdateForCurrentUser(ObservationSubjects.LOGOUT, this.getClass().getName());
		}catch(NoLoggedInUserException ignored){};
	}
	
	@Override
	public String getLogedUserId() throws NoLoggedInUserException {
		if(!isLogedIn())
			throw new APIException("No loged in users!");
		return getCallContext().getCurrentUserId();
	}
	
	@Override public boolean isLogedIn() throws APIException {
		try{
			return !StringUtils.isEmpty(getCallContext().getCurrentUserId());
		}catch(NoLoggedInUserException e){
			return false;
		}
	}

	public void addLogoutPostprocessor(LogoutPostProcessor postProcessor) {
		logoutPostProcessors.add(postProcessor);
	}

	public void addLogoutPreprocessor(LogoutPreProcessor preProcessor) {
		logoutPreProcessors.add(preProcessor);
		
	}
	
	//////////
	private void callLoginPreprocessors(String userId) throws APIException{
		for (LoginPreProcessor p : loginPreProcessors){
			try{
				p.preProcessLogin(userId);
			}catch(ProcessorException e){
				throw e;
			}catch(Exception e){
				log.error("Exception in loginpreprocessor: "+p,e);
			}
		}
	}
	
	private void callLoginPostprocessors(String userId) {
		for (LoginPostProcessor p : loginPostProcessors){
			try{
				p.postProcessLogin(userId);
			}catch(Exception e){
				log.error("Exception in loginpostprocessor: "+p,e);
			}
		}
	}

	private void callLogoutPreprocessors(String userId) throws APIException{
		for (LogoutPreProcessor p : logoutPreProcessors){
			try{
				p.preProcessLogout(userId);
			}catch(ProcessorException e){
				throw e;
			}catch(Exception e){
				log.error("Exception in logoutPreProcessor: "+p,e);
			}
		}
	}

	private void callLogoutPostprocessors(String userId) {
		for (LogoutPostProcessor p : logoutPostProcessors){
			try{
				p.postProcessLogout(userId);
			}catch(Exception e){
				log.error("Exception in logoutpostprocessor: "+p,e);
			}
		}
		
	}

	
	

}
