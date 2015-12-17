package com.he.events

import grails.events.Events
import grails.transaction.Transactional
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.events.ClosureEventConsumer
import reactor.bus.Event
import reactor.bus.registry.Registration
import reactor.spring.context.annotation.Consumer
import reactor.spring.context.annotation.Selector

import javax.annotation.PostConstruct

@Transactional
@Consumer
class EventService{

   int counter = 0;

    @PostConstruct
    def serviceMethod() {
        on("gorm:preInsert") { PreInsertEvent event ->
            print "GORM CLOSURE GOT EVENT ${event.entityObject.class.name}"
            def eobj = event.entityObject
            if(eobj.class.simpleName=="Car"){
                println(" | " + eobj.properties)
            }else{
                println("\tNot Car: "+ eobj)
            }
        }

        on("car:new") { Car car ->
           println "CAR  CLOSURE GOT EVENT: $car | ${car.brand}"
        }
    }

    @Selector('gorm:preInsert')
    void gormListener(PreInsertEvent data) {
        println "GORM LISTENER GOT EVENT $data"
    }

    @Selector('car:new')
    void newCarListener(Car car) {
        counter++
        println "CAR LISTENER: ($counter) : $car"
    }

    @Selector('bus:new')
    void newBusListener(Bus bus) {
        println "BUS LISTENER: $bus"  
    }
}
