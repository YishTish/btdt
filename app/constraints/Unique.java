package constraints;
import constraints.impl.UniqueValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import play.db.ebean.*;
/**
 * Validation annotation to check unique constraint/
 *
 * An array of fields can be supplied.
 *
 * Example, check uniqueness on code
 * @Unique(modelClass = User.class, fields = {"code"}, message = "Record has already been taken")
 *
 * Example, check uniqueness on more fields
 * @Unique.List({
 *   @Unique(modelClass = User.class, fields = {"code"}, message = "Code has already been taken"),
     @Unique(modelClass = User.class, fields = {"name"}, message = "Name has already been taken"),
     @Unique(modelClass = User.class, fields = {"name", "published"}, message = "Name has already been taken on published status")
 * })
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
@Documented
public @interface Unique
{
    String message() default "{constraints.unique}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Model> modelClass();
    Class<?> idClass() default Long.class;
    /**
     * @return The fields
     */
    String[] fields();
    /**
     * Defines several <code>@Unique</code> annotations on the same element
     *
     * @see Unique
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
            @interface List
    {
        Unique[] value();
    }
}