package com.pb.sawdust.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * The {@code Transient} annotation is used to specify classes which might belong in a certain package, but need not be
 * included in distributions. Examples, tests, and experiments are all possible cases where this annotation might be applied.
 * Generally, the level of code quality, testing, and documentation will be much lower for a class with this annotation.
 *
 * @author crf <br/>
 *         Started 7/26/11 12:05 PM
 */
@Inherited
@Target({ElementType.TYPE})
public @interface Transient {
}