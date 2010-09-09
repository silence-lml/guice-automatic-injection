package de.devsurf.injection.guice.sonatype.example.autobind.multiple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import de.devsurf.injection.guice.asm.example.autobind.multiple.Example;

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
