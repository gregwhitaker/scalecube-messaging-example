# scalecube-messaging-example
An example of sending information between service nodes using [ScaleCube](https://scalecube.io).

## Building the Example
Run the following command to build the example:

    ./gradlew clean build
    
## Running the Example
Run the following command to execute the example application:

    ./gradlew run
    
If successful, you will see `3` services start with a `10` second interval in between them and they will start sending messages
between one another:

    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Sally] Received Message: 'Bob3' from '192.168.254.49:7001'
    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Carol] Received Message: 'Bob27' from '192.168.254.49:7001'
    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Bob] Received Message: 'Sally4' from '192.168.254.49:50683'
    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Carol] Received Message: 'Sally16' from '192.168.254.49:50683'
    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Bob] Received Message: 'Carol30' from '192.168.254.49:50677'
    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Sally] Received Message: 'Carol53' from '192.168.254.49:50677'
    [sc-cluster-io-nio-1] INFO example.scalecube.messaging.Service - [Sally] Received Message: 'Bob59' from '192.168.254.49:7001'

## Bugs and Feedback
For bugs, questions, feedback, and discussions please use the [Github Issues](https://github.com/gregwhitaker/scalecube-messaging-example/issues).

## License
MIT License

Copyright (c) 2020 Greg Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
