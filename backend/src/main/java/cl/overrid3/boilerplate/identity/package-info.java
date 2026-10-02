/**
 * Identidad local desacoplada del proveedor.
 * WorkOS aporta el sujeto y la organización del JWT; las tablas propias permiten cambiar de IdP.
 */
@org.springframework.modulith.ApplicationModule(displayName = "Identidad", allowedDependencies = {})
package cl.overrid3.boilerplate.identity;
