package com.he.events

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class BusController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Bus.list(params), model:[busCount: Bus.count()]
    }

    def show(Bus bus) {
        respond bus
    }

    def create() {
        respond new Bus(params)
    }

    @Transactional
    def save(Bus bus) {
        if (bus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (bus.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond bus.errors, view:'create'
            return
        }

        bus.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'bus.label', default: 'Bus'), bus.id])
                redirect bus
            }
            '*' { respond bus, [status: CREATED] }
        }
    }

    def edit(Bus bus) {
        respond bus
    }

    @Transactional
    def update(Bus bus) {
        if (bus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (bus.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond bus.errors, view:'edit'
            return
        }

        bus.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'bus.label', default: 'Bus'), bus.id])
                redirect bus
            }
            '*'{ respond bus, [status: OK] }
        }
    }

    @Transactional
    def delete(Bus bus) {

        if (bus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        bus.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'bus.label', default: 'Bus'), bus.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'bus.label', default: 'Bus'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
