package de.devsurf.injection.guice.reflections.example.autobind.multiple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

public class ExampleContainer {
    private List<Example> _examples;
    
    @Inject
    public ExampleContainer(Set<Example> example) {
	_examples = new ArrayList<Example>(example);
    }
    
    public void sayHello(){
	for(Example example : _examples){
	    System.out.println(example.sayHello());
	}
    }
}
