package com.pb.sawdust.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * The {@code Beta} annotation is used to identify classes, methods, <i>etc.</i> which are in an early (or unstable) development
 * stage, and generally should not be used for production (shippable) level products.
 *
 * @author crf <br/>
 *         Started 1/17/11 9:26 PM
 */
@Inherited
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD,ElementType.TYPE})
public @interface Beta {
}