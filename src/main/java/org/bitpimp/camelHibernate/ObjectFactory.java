package org.bitpimp.camelHibernate;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public static Person createPerson() {
        return new Person();
    }
}
