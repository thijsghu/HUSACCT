package husacct.validate.domain.factory.violationtype.java;

import husacct.validate.domain.ConfigurationServiceImpl;

import husacct.validate.domain.exception.SeverityNotFoundException;
import husacct.validate.domain.exception.ViolationTypeNotFoundException;
import husacct.validate.domain.validation.Severity;
import husacct.validate.domain.validation.ViolationType;
import husacct.validate.domain.validation.iternal_tranfer_objects.CategoryKeySeverityDTO;
import husacct.validate.domain.validation.ruletype.RuleTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractViolationType {
	private Logger logger = Logger.getLogger(AbstractViolationType.class);
	private final ConfigurationServiceImpl configuration;
	protected List<CategoryKeySeverityDTO> allViolationKeys;
	protected ViolationtypeGenerator generator;
	protected String languageName;

	public abstract List<ViolationType> createViolationTypesByRule(String key);
	public abstract HashMap<String, List<ViolationType>> getAllViolationTypes();

	AbstractViolationType(ConfigurationServiceImpl configuration, String languageName){
		this.configuration = configuration;
		this.languageName = languageName;
		this.generator = new ViolationtypeGenerator();		
	}

	protected List<ViolationType> generateViolationTypes(EnumSet<?> enums){
		List<ViolationType> violationtypes = new ArrayList<ViolationType>();
		for(Enum<?> enumValue : enums){			
			ViolationType violationtype = generateViolationType(enumValue);	
			violationtypes.add(violationtype);
		}
		return violationtypes;
	}

	protected HashMap<String, List<ViolationType>> getAllViolationTypes(List<CategoryKeySeverityDTO> keyList){
		HashMap<String, List<ViolationType>> categoryViolations = new HashMap<String, List<ViolationType>>();
		for(CategoryKeySeverityDTO dto : keyList){
			if(categoryViolations.containsKey(dto.getCategory())){
				List<ViolationType> violationtypes = categoryViolations.get(dto.getCategory());
				ViolationType violationtype = createViolationType(dto.getKey());
				violationtypes.add(violationtype);
			}
			else{
				List<ViolationType> violationtypes = new ArrayList<ViolationType>();
				ViolationType violationtype = createViolationType(dto.getKey());
				violationtypes.add(violationtype);	
				categoryViolations.put(dto.getCategory(), violationtypes);
			}
		}
		return categoryViolations;
	}

	public ViolationType createViolationType(String violationKey){
		List<String> violationKeysToLower = new ArrayList<String>();
		for(CategoryKeySeverityDTO violationtype : allViolationKeys){
			violationKeysToLower.add(violationtype.getKey().toLowerCase());
		}		

		if(violationKeysToLower.contains(violationKey.toLowerCase())){
			final Severity severity = createSeverity(languageName, violationKey);
			return new ViolationType(violationKey, severity);
		}
		else{
			logger.warn(String.format("Warning specified %s not found in the system", violationKey));			
		}
		throw new ViolationTypeNotFoundException();
	}

	private ViolationType generateViolationType(Enum<?> enumValue){		
		final Severity severity = createSeverity(languageName, enumValue.toString());
		return new ViolationType(enumValue.toString(), severity);
	}

	protected boolean isCategoryLegalityOfDependency(String ruleTypeKey){
		if(ruleTypeKey.equals(RuleTypes.IS_ONLY_ALLOWED.toString()) || ruleTypeKey.equals(RuleTypes.IS_NOT_ALLOWED.toString()) || ruleTypeKey.equals(RuleTypes.IS_ALLOWED.toString()) || ruleTypeKey.equals(RuleTypes.IS_NOT_ALLOWED.toString())||ruleTypeKey.equals(RuleTypes.IS_ONLY_MODULE_ALLOWED.toString())||ruleTypeKey.equals(RuleTypes.MUST_USE.toString())||ruleTypeKey.equals(RuleTypes.BACK_CALL.toString())||ruleTypeKey.equals(RuleTypes.SKIP_CALL.toString())){
			return true;
		}
		else {
			return false;
		}
	}

	protected boolean isVisibilityConvenctionRule(String ruleTypKey){
		if(ruleTypKey.equals(RuleTypes.VISIBILITY_CONVENTION.toString())){
			return true;
		}
		else{ 
			return false;		
		}
	}

	protected boolean isNamingConvention(String ruleTypeKey){
		if(ruleTypeKey.equals(RuleTypes.NAMING_CONVENTION.toString())){
			return true;
		}
		else{ 
			return false;
		}
	}

	protected boolean isLoopsInModule(String ruleTypeKey){
		if(ruleTypeKey.equals(RuleTypes.LOOPS_IN_MODULE.toString())){
			return true;
		}
		else{
			return false;
		}
	}

	private Severity createSeverity(String programmingLanguage, String violationKey){
		try{
			return configuration.getSeverityFromKey(programmingLanguage, violationKey);			
		}catch(SeverityNotFoundException e){
			CategoryKeySeverityDTO violation = getCategoryKeySeverityDTO(violationKey);
			if(violation != null){
				return configuration.getSeverityByName(violation.getDefaultSeverity().toString());

			}
		}	
		return null;
	}

	private CategoryKeySeverityDTO getCategoryKeySeverityDTO(String violationKey){		
		for(CategoryKeySeverityDTO violation : allViolationKeys){
			if(violation.getKey().toLowerCase().equals(violationKey.toLowerCase())){				
				return violation;
			}
		}
		return null;		
	}
}