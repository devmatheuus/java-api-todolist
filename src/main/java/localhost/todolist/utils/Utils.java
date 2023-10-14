package localhost.todolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullProperties(src));
    }
    public static String[] getNullProperties(Object object) {
        final BeanWrapper source = new BeanWrapperImpl(object);


        PropertyDescriptor[] properties = source.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for(PropertyDescriptor property: properties) {
            Object value = source.getPropertyValue(property.getName());

            if (value == null) {
                emptyNames.add(property.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
