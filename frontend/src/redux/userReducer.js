const initialState = {
    id: null,
    token: null,
    role: null,
    name: null
  };
  
  const userReducer = (state = initialState, action) => {
    switch (action.type) {
      case 'SET_USER':
        return {
          ...state,
          id: action.payload.id,
          token: action.payload.token,
          role: action.payload.role,
          name: action.payload.name
        };
      case 'SET_TYPE':
        return {
            ...state,
            [action.payload.type]: action.payload.value
        }
      case 'LOGOUT':
        return {
          ...initialState
        };
      default:
        return state;
    }
  };
  
  export default userReducer;