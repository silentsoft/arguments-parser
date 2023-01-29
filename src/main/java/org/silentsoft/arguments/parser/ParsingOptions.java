package org.silentsoft.arguments.parser;

/**
 * Parsing Options with dash prefix
 * <ul>
 * <li>LEAVE_DASH_PREFIX <b>(default)</b></li>
 * <li>CASE_SENSITIVE_SINGLE_DASH <b>(default)</b></li>
 * <li>CASE_INSENSITIVE_SINGLE_DASH</li>
 * <li>CASE_SENSITIVE_DOUBLE_DASH</li>
 * <li>CASE_INSENSITIVE_DOUBLE_DASH <b>(default)</b></li>
 * </ul>
 *
 * Parsing Options without dash prefix
 * <ul>
 * <li>REMOVE_DASH_PREFIX</li>
 * <li>CASE_SENSITIVE</li>
 * <li>CASE_INSENSITIVE</li>
 * </ul>
 */
public enum ParsingOptions {
    LEAVE_DASH_PREFIX,
    CASE_SENSITIVE_SINGLE_DASH,
    CASE_INSENSITIVE_SINGLE_DASH,
    CASE_SENSITIVE_DOUBLE_DASH,
    CASE_INSENSITIVE_DOUBLE_DASH,

    REMOVE_DASH_PREFIX,
    CASE_SENSITIVE,
    CASE_INSENSITIVE,
}
