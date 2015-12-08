import serial, time, argparse, sys, os, re, time
from sys import argv

parser = argparse.ArgumentParser()
parser.add_argument('device', help='specify device on serial port (ex. /dev/ttyUSB0)')
parser.add_argument('-t', '--time', help='time in sec until shutdown(default=100)', type=int, default=100, metavar='T')
parser.add_argument('-b', '--bandwith', help='bandwith for serial port(default=9600)', type=int, default=9600, metavar='B')
parser.add_argument('-d', '--directory', help='output directory', metavar='PATH')

args = parser.parse_args()

try:
    if args.directory is None:
        args.directory = os.path.curdir
    if os.path.isdir(args.directory):
        base_dir = os.path.abspath(args.directory)
    else:
        base_dir = os.path.join(os.path.abspath(os.path.curdir), args.directory)
    if not os.path.isdir(base_dir):
        os.makedirs(base_dir)

    print 'Starting...'

    ser = serial.Serial(args.device, 9600)

    files = {}
    files['datadump'] = open(os.path.join(base_dir, 'dump.dat'), 'w')
    files['other'] = open(os.path.join(base_dir, 'other.dat'), 'w')

    # last_increment = time.time()
    # counter = 0
    # increment_delay = 1.0

    print 'Dumping existing data...'

    while ser.inWaiting() > 0:
        ser.readline()

    print 'Dumping existing data...done'

    finish_time = time.time() + args.time
    start_time = time.time()

    print 'Begining reading data from serial port on {}'.format(args.device)

    while time.time() < finish_time:

        print '\rCollecting data {} sec left'.format(finish_time - time.time()),

        if ser.inWaiting() > 0:
            # if time.time() - last_increment > increment_delay:
            #    counter += 1
            #    last_increment = time.time()
            line = ser.readline()
        else:
            continue

        m = re.match('\$(\w{5}),', line)
        if m is None:
            filename = 'other'
        else:
            filename = m.groups()[0].lower()

        if not files.has_key(filename):
            files[filename] = open(os.path.join(base_dir, filename +'.dat'), 'w')

        f = files[filename]

        counter = int(time.time() - start_time)
        f.write('{}: {}'.format(counter, line))
        files['datadump'].write('{}: {}'.format(counter, line))

    print '\rCollecting data' + '.'*30 + 'done'

except:

    print 'Unexpected error:', sys.exc_info()[1]
    print 'Check input params!!!'
    parser.print_help()

else:

    print 'Successfully completed...'

finally:

    print 'Closing files...'

    for f in files.values():
        f.close()

    print 'Done...'


