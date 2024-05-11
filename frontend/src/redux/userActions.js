export const setUser = (userData) => {
    return {
      type: 'SET_USER',
      payload: userData
    };
};
  
export const logout = () => {
    return {
        type: 'LOGOUT'
    };
};

export const setType = (type, value) => {
    return {
        type: 'SET_TYPE',
        payload: {
            type: type,
            value: value
        }
    }
}
