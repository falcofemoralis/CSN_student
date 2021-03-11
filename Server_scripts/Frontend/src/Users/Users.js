import React, { Component } from 'react'
import './Users.css';

export default class Users extends Component {
    constructor(props) {
        super(props);
        this.state = { users: [] };
    }

    componentDidMount() {
        console.log("fetch!");
        fetch('http://192.168.0.104:81/api/users/all', {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                console.log("ok");
                response.json().then((users) => {
                    this.setState({ users: users });
                });
            }
        });
    }

    render() {
        return (
            <div className="users">
                <ul className="users__list">
                    <li className="users__list-item">
                        <p className="users__list-item-name">Никнейм</p>
                        <p className="users__list-item-opens">Кол-во входов</p>
                    </li>
                    {this.state.users.map((user) =>
                        <li className="users__list-item" key={user.NickName}>
                            <p className="users__list-item-name table-text">{user.NickName}</p>
                            <p className="users__list-item-opens table-text">{user.Visits}</p>
                        </li>
                    )}
                </ul>
            </div>
        )
    }
}
