package com.he.events

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CarController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Car.list(params), model:[carCount: Car.count()]
    }

    def show(Car car) {
        respond car
    }

    def create() {
        respond new Car(params)
    }

    @Transactional
    def save(Car car) {
        if (car == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (car.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond car.errors, view:'create'
            return
        }

        car.save flush:true

        notify "car:new", eventFor(car)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'car.label', default: 'Car'), car.id])
                redirect car
            }
            '*' { respond car, [status: CREATED] }
        }
    }

    def edit(Car car) {
        respond car
    }

    @Transactional
    def update(Car car) {
        if (car == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (car.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond car.errors, view:'edit'
            return
        }

        car.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'car.label', default: 'Car'), car.id])
                redirect car
            }
            '*'{ respond car, [status: OK] }
        }
    }

    @Transactional
    def delete(Car car) {

        if (car == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        car.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'car.label', default: 'Car'), car.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'car.label', default: 'Car'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
