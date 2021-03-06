//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.7
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2020.10.05 at 10:58:10 AM IST
//

package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for ActMoodPredicate.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>
 *
 * <pre>
 * &lt;simpleType name="ActMoodPredicate">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EVN.CRT"/>
 *     &lt;enumeration value="GOL"/>
 *     &lt;enumeration value="OPT"/>
 *     &lt;enumeration value="PERM"/>
 *     &lt;enumeration value="PERMRQ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "ActMoodPredicate")
@XmlEnum
public enum ActMoodPredicate {
  @XmlEnumValue("EVN.CRT")
  EVN_CRT("EVN.CRT"),
  GOL("GOL"),
  OPT("OPT"),
  PERM("PERM"),
  PERMRQ("PERMRQ");
  private final String value;

  ActMoodPredicate(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static ActMoodPredicate fromValue(String v) {
    for (ActMoodPredicate c : ActMoodPredicate.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }
}
