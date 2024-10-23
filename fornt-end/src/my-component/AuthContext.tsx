import { jwtDecode } from 'jwt-decode';
import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';

interface AuthContextType {
  isLoggedIn: boolean;
  setIsLoggedIn: (loggedIn: boolean) => void;
  sssAccessToken?: string; // optional로 변경
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  //const [sssAccessToken, setSssAccessToken] = useState<string | undefined>(undefined); // string으로 변경

  // 유효시간 만료 체크
  useEffect(() => {
    const token = localStorage.getItem('sssAccessToken');

    if (token) {
      //setSssAccessToken(token);
      const decodedAccessToken = jwtDecode<{ exp?: number }>(token); // 타입 명시
      const currentTime = Math.floor(Date.now() / 1000); // 현재 시간을 초 단위로 변환
      setIsLoggedIn(decodedAccessToken.exp !== undefined && decodedAccessToken.exp > currentTime);
    }

  }, []);

  return (
    <AuthContext.Provider value={{ isLoggedIn, setIsLoggedIn }}>
      {children}
    </AuthContext.Provider>
  );
};
