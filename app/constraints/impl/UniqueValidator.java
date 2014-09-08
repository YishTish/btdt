package constraints.impl;
import constraints.Unique;
import org.apache.commons.beanutils.BeanUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import play.db.ebean.*;
import com.avaje.ebean.ExpressionList;
public class UniqueValidator implements ConstraintValidator<Unique, Object>
{
    private Class<? extends Model> modelClass;
    private Class<?> idClass;
    private String[] fields;
    @Override
    public void initialize(final Unique constraintAnnotation)
    {
        this.modelClass = constraintAnnotation.modelClass();
        this.idClass = constraintAnnotation.idClass();
        fields = constraintAnnotation.fields();
    }
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try
        {
            Model.Finder<?, ? extends Model> find = new Model.Finder(idClass, modelClass);
            ExpressionList el = find.where();
            for (String f : fields) {
                el.eq(f, BeanUtils.getProperty(value, f));
            }
            Model record = (Model) el.findUnique();
            boolean recordFound = record != null;
            boolean sameRecord = false;
            boolean recordValid = false;
            Long modelId = null;
            Long currentId = null;
            if (recordFound) {
                modelId = new Long(BeanUtils.getProperty(record, "id"));
                try {
                    currentId = new Long(BeanUtils.getProperty(value, "id"));
                } catch (Exception e) {
                    currentId = new Long(0);
                }
                sameRecord = currentId.longValue() == modelId.longValue();
            }
            recordValid = !recordFound || sameRecord;
            // Some debug...
            // System.out.println(">>> searching name = " + BeanUtils.getProperty(record, "name")); 
            // System.out.println(">>> record found ? " + recordFound); 
            // System.out.println(">>> same record ? " + sameRecord); 
            // System.out.println(">>> record valid ? " + recordValid;
            return recordValid;
        }
        catch (final Exception ignore)
        {
            // FIXME: ignored exception, WARNING...
        }
        return true;
    }
}