import pika
import sys
import argparse

def parseArguments():

    #Create argument parser
    parser = argparse.ArgumentParser()

    parser.add_argument("exchangeType", help="Broker exchange type [none] is default exchange type", type=str)
    parser.add_argument("-host", "--host", help="IP address of Broker server", type=str, default="localhost")
    parser.add_argument("-port", "--port", help="Port of the broker", type=int, default=5672)
    parser.add_argument("-u", "--username", help="Username", type=str, default="guest")
    parser.add_argument("-p", "--password", help="User password", type=str, default="guest")
    parser.add_argument("-vh", "--virtualHost", help="RabbitMQ Virtual Host", type=str, default="/")
    parser.add_argument("-e", "--exchangeName", help="Exchange name", type=str, default="")
    parser.add_argument("-cd", "--channelDurable", help="Channel Durability", type=bool, default=True)
    parser.add_argument("-k", "--routingKey", help="Routing Key", type=str, default="")
    parser.add_argument("-q", "--queueName", help="Exchange name", type=str, default="")

    parser.add_argument("--version", action="version", version='%(prog)s - Version 1.0')

    args = parser.parse_args()

    return args

if __name__ == '__main__':

    def callback(ch, method, properties, body):
        print(" [x] %r:%r" % (method.routing_key, body))

    args = parseArguments()
    
    ## Create connection
    credentials = pika.PlainCredentials(args.username, args.password)
    conn = pika.BlockingConnection(pika.ConnectionParameters(args.host, args.port, args.virtualHost, credentials))

    channel = conn.channel()
    exType = args.exchangeType
    if args.exchangeType == "none":
        exType = ''

    if not args.exchangeType == "none":
        result = channel.queue_declare(exclusive=True)
        queue_name = result.method.queue

        channel.queue_bind(exchange=args.exchangeName, queue=queue_name, routing_key=args.routingKey)
        channel.basic_consume(callback, queue=queue_name, no_ack=True)

    if args.exchangeType == "none":
        channel.queue_bind(exchange=args.exchangeName, queue=args.queueName, routing_key=args.routingKey)
        channel.basic_consume(callback, queue=args.queueName, no_ack=True)
        
    print(" [*] Waiting for logs...")
    channel.start_consuming()