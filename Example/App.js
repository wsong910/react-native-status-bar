/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';
import {
    StyleSheet,
    ScrollView,
    Text,
    Navigator
}
    from 'react-native';
// import CustomerComponents, {Navigator} from 'react-native-deprecated-custom-components';

const USER_MODELS = {
    1: {name: 'wu', age: 23},
    2: {name: 'song', age: 30}
};

export default class TestNavigator extends Component<{}>{

    render() {
        let defaultName = 'List';
        let defaultComponent = List;

        return (
            <Navigator
                initialRoute={{name: defaultName, component: defaultComponent}}

                configureScene={
                    (route) => {
                        return Navigator.SceneConfigs.VerticalDownSwipeJum;
                    }
                }

                renderScene={
                    (route, navigator) => {
                        let Component = route.component;
                        return <Component {...route.params} navigator={navigator}/>
                    }
                }
            />
        );
    }
}


class List extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 1,
            user: null,
        };
    }

    _pressButton() {
        const {navigator} = this.props;
        const self = this;
        if (navigator) {
            navigator.push({
                name: 'Detail',
                component: Detail,
                params: {
                    id: this.state.id,
                    getUser: function (user) {
                        self.setState({user: user})
                    }
                }
            })
        }
    }

    render() {
        if (this.state.user) {
            return (
                <Text style={styles.list_item}>用户信息：{JSON.stringify(this.state.user)}</Text>
            );
        } else {
            return (
                <ScrollView style={styles.flex}>
                    <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>☆ 豪华邮轮济州岛3日游</Text>
                    <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>☆ 豪华邮轮台湾3日游</Text>
                    <Text style={styles.list_item} onPress={this._pressButton()}>☆ 豪华邮轮台湾3日游</Text>
                </ScrollView>
            );
        }
    }


}

class Detail extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: null
        };
    }

    componentDidMount() {
        this.setState({
            id: this.props.id
        });
    }


    _pressButton() {
        const {navigator} = this.props;
        if (this.props.getUser) {
            let user = USER_MODELS[this.props.id];
            this.props.getUser(user);
        }
        if (navigator) {
            navigator.pop();
        }
    }

    render() {
        return (
            <ScrollView>
                <Text style={styles.list_item}> 传递来的用户id是 ：{this.state.id}</Text>
                <Text style={styles.list_item} onPress={this._pressButton().bind(this)}>点击我可以跳回去</Text>
            </ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    flex: {
        flex: 1
    },

    list_item: {
        height: 40,
        marginLeft: 10,
        marginRight: 10,
        fontSize: 20,
        borderBottomColor: '#ddd',
        justifyContent: 'center'
    }

});


