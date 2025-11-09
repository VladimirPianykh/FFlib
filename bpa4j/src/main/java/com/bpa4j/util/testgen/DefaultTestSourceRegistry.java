package com.bpa4j.util.testgen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Registry for default field value sources.
 * @author AI-generated
 */
public final class DefaultTestSourceRegistry{
	public static final String[]names = {
    	"Алиса", "Боря", "Дима", "Оля", "Егор", "Таня", "Витя", "Костя", "Юля", "Стас", "Галя",
    	"Марк", "Настя", "Федя", "Ира", "Мила", "Сева", "Лера", "Паша", "Лёва", "Ника",
    	"Тим", "Рита", "Жора", "Аня", "Вика", "Гоша", "Рома", "Сава", "Зоя", "Яна",
    	"Макс", "Лиза", "Рената", "Саша", "Катя", "Варя", "Гена", "Нина", "Арс", "Алла",
    	"Илья", "Вова", "Соня", "Лёша", "Женя", "Петя", "Олег", "Юра", "Маша",
    	"Лена", "Даша", "Толя", "Надя", "Лика", "Тима", "Кира", "Глеб", "Миша",
    	"Люба", "Вера", "Аркаша", "Мария", "Семен", "Света", "Ваня", "Ярик", "Филя"
    };
    private static final Map<Class<?>,Supplier<?>> DEFAULT_SOURCES=new HashMap<>();
	static{
		DEFAULT_SOURCES.put(String.class,()->{
			int r=(int)(Math.random()*names.length);
			return names[r];
		});
		DEFAULT_SOURCES.put(Integer.class,()->(int)(Math.random()*1000));
		DEFAULT_SOURCES.put(int.class,()->(int)(Math.random()*1000));
		DEFAULT_SOURCES.put(Double.class,()->Math.random()*1000);
		DEFAULT_SOURCES.put(double.class,()->Math.random()*1000);
		DEFAULT_SOURCES.put(Boolean.class,()->Math.random()>0.5);
		DEFAULT_SOURCES.put(boolean.class,()->Math.random()>0.5);
		DEFAULT_SOURCES.put(Long.class,()->(long)(Math.random()*10000));
		DEFAULT_SOURCES.put(long.class,()->(long)(Math.random()*10000));
		DEFAULT_SOURCES.put(Float.class,()->(float)(Math.random()*1000));
		DEFAULT_SOURCES.put(float.class,()->(float)(Math.random()*1000));
		DEFAULT_SOURCES.put(List.class,ArrayList::new);
		DEFAULT_SOURCES.put(ArrayList.class,ArrayList::new);
		DEFAULT_SOURCES.put(Map.class,HashMap::new);
		DEFAULT_SOURCES.put(HashMap.class,HashMap::new);
		DEFAULT_SOURCES.put(Set.class,HashSet::new);
		DEFAULT_SOURCES.put(HashSet.class,HashSet::new);
	}
	private DefaultTestSourceRegistry(){}
	public static <T> Supplier<T> getDefaultSource(Class<T> type){
		@SuppressWarnings("unchecked")
		Supplier<T> supplier=(Supplier<T>)DEFAULT_SOURCES.get(type);
		return supplier;
	}
	public static boolean hasDefaultSource(Class<?> type){
		return DEFAULT_SOURCES.containsKey(type);
	}
	public static Set<Class<?>> getRegisteredTypes(){
		return new HashSet<>(DEFAULT_SOURCES.keySet());
	}
}

