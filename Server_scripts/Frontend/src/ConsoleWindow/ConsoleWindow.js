import React, { Component } from 'react'
import './ConsoleWindow.css';

export default class ConsoleWindow extends Component {
    constructor(props) {
        super(props);

        this.state = { logs: [] };
    }

    componentDidUpdate(prevProps) {
        if (prevProps.logs !== this.props.logs && this.props.logs) {
            const logs = this.state.logs;
            const newLogs = this.props.logs.split('<br>');
            this.setState({ logs: logs.concat(newLogs) });
        }
    }

    render() {
        const logsElements = [];
        const logs = this.state.logs;
        for (let i = logs.length - 1; i >= 0; i--) {
            if (logs[i]) logsElements.push(<li key={i}>{(new Date).toTimeString()}: {logs[i]}</li>);
        }
        return (
            <div>
                <ul className="logs-list">
                    {logsElements}
                </ul>
            </div>
        )
    }
}
